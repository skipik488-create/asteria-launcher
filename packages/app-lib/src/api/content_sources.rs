//! Content source capability discovery.

use crate::util::fetch::INSECURE_REQWEST_CLIENT;
use serde::{Deserialize, Serialize};

#[derive(Debug, Clone, Copy, Serialize)]
#[serde(rename_all = "snake_case")]
pub enum ContentSource {
    Modrinth,
    CurseForge,
}

#[derive(Debug, Clone, Serialize)]
pub struct ContentSourceCapability {
    pub source: ContentSource,
    pub search: bool,
    pub install: bool,
    pub requires_api_key: bool,
    pub configured: bool,
}

#[derive(Debug, Clone, Deserialize, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct CurseForgeAuthor {
    pub name: String,
}

#[derive(Debug, Clone, Deserialize, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct CurseForgeCategory {
    pub name: String,
}

#[derive(Debug, Clone, Deserialize, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct CurseForgeLogo {
    pub thumbnail_url: Option<String>,
    pub url: Option<String>,
}

#[derive(Debug, Clone, Deserialize, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct CurseForgeLatestFileIndex {
    pub game_version: String,
    pub file_id: u64,
    pub mod_loader: Option<u32>,
}

#[derive(Debug, Clone, Deserialize, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct CurseForgeProject {
    pub id: u64,
    pub name: String,
    pub slug: String,
    pub summary: String,
    pub download_count: u64,
    pub date_created: String,
    pub date_modified: String,
    pub authors: Vec<CurseForgeAuthor>,
    pub categories: Vec<CurseForgeCategory>,
    pub logo: Option<CurseForgeLogo>,
    #[serde(default)]
    pub latest_files_indexes: Vec<CurseForgeLatestFileIndex>,
}

#[derive(Debug, Clone, Deserialize)]
#[serde(rename_all = "camelCase")]
struct CurseForgePagination {
    index: u32,
    page_size: u32,
    total_count: u64,
}

#[derive(Debug, Clone, Deserialize)]
struct CurseForgeResponse {
    data: Vec<CurseForgeProject>,
    pagination: CurseForgePagination,
}

#[derive(Debug, Clone, Serialize)]
pub struct CurseForgeSearchResults {
    pub projects: Vec<CurseForgeProject>,
    pub index: u32,
    pub page_size: u32,
    pub total_count: u64,
}

pub fn capabilities() -> Vec<ContentSourceCapability> {
    let curseforge_configured = std::env::var("CURSEFORGE_API_KEY")
        .is_ok_and(|key| !key.trim().is_empty());

    vec![
        ContentSourceCapability {
            source: ContentSource::Modrinth,
            search: true,
            install: true,
            requires_api_key: false,
            configured: true,
        },
        ContentSourceCapability {
            source: ContentSource::CurseForge,
            search: curseforge_configured,
            install: false,
            requires_api_key: true,
            configured: curseforge_configured,
        },
    ]
}

pub async fn search_curseforge(
    query: &str,
    project_type: &str,
    game_version: Option<&str>,
    loader: Option<&str>,
    index: u32,
    page_size: u32,
) -> crate::Result<CurseForgeSearchResults> {
    let api_key = std::env::var("CURSEFORGE_API_KEY")
        .ok()
        .filter(|key| !key.trim().is_empty())
        .ok_or_else(|| {
            crate::ErrorKind::InputError(
                "CurseForge API key is not configured".to_string(),
            )
            .as_error()
        })?;

    let class_id = match project_type {
        "modpack" => Some(4471),
        "mod" => Some(6),
        "resourcepack" => Some(12),
        "shader" => Some(6552),
        _ => None,
    };
    let loader_type = loader.and_then(|value| match value {
        "forge" => Some(1),
        "fabric" => Some(4),
        "quilt" => Some(5),
        "neoforge" => Some(6),
        _ => None,
    });

    let mut params = vec![
        ("gameId", "432".to_string()),
        ("searchFilter", query.to_string()),
        ("index", index.to_string()),
        ("pageSize", page_size.clamp(1, 50).to_string()),
        ("sortField", "2".to_string()),
        ("sortOrder", "desc".to_string()),
    ];
    if let Some(class_id) = class_id {
        params.push(("classId", class_id.to_string()));
    }
    if let Some(game_version) = game_version.filter(|value| !value.is_empty()) {
        params.push(("gameVersion", game_version.to_string()));
    }
    if let Some(loader_type) = loader_type {
        params.push(("modLoaderType", loader_type.to_string()));
    }

    let response = INSECURE_REQWEST_CLIENT
        .get("https://api.curseforge.com/v1/mods/search")
        .header("x-api-key", api_key)
        .query(&params)
        .send()
        .await?
        .error_for_status()?
        .json::<CurseForgeResponse>()
        .await?;

    Ok(CurseForgeSearchResults {
        projects: response.data,
        index: response.pagination.index,
        page_size: response.pagination.page_size,
        total_count: response.pagination.total_count,
    })
}
