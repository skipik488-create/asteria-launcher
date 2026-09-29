use crate::api::Result;
use tauri::Runtime;
use theseus::content_sources::{
    ContentSourceCapability, CurseForgeSearchResults,
};
use theseus::prelude::{ModLoader, ProjectType};

const ASTERIA_CLIENT_VERSION: &str = "0.1.0";
const ASTERIA_CLIENT_GAME_VERSION: &str = "26.3";
const ASTERIA_CLIENT_JAR: &[u8] =
    include_bytes!("../../resources/asteria-client-0.1.0.jar");

pub fn init<R: Runtime>() -> tauri::plugin::TauriPlugin<R> {
    tauri::plugin::Builder::new("content_sources")
        .invoke_handler(tauri::generate_handler![
            capabilities,
            search_curseforge,
            install_asteria_client
        ])
        .build()
}

#[tauri::command]
pub async fn capabilities() -> Result<Vec<ContentSourceCapability>> {
    Ok(theseus::content_sources::capabilities())
}

#[tauri::command]
pub async fn search_curseforge(
    query: String,
    project_type: String,
    game_version: Option<String>,
    loader: Option<String>,
    index: u32,
    page_size: u32,
) -> Result<CurseForgeSearchResults> {
    Ok(theseus::content_sources::search_curseforge(
        &query,
        &project_type,
        game_version.as_deref(),
        loader.as_deref(),
        index,
        page_size,
    )
    .await?)
}

#[tauri::command]
pub async fn install_asteria_client(instance_id: String) -> Result<String> {
    let metadata =
        theseus::instance::get(&instance_id).await?.ok_or_else(|| {
            theseus::Error::from(theseus::ErrorKind::InputError(
                "Unknown instance".to_string(),
            ))
        })?;

    if metadata.applied_content_set.loader != ModLoader::Fabric
        || metadata.applied_content_set.game_version
            != ASTERIA_CLIENT_GAME_VERSION
    {
        return Err(theseus::Error::from(
            theseus::ErrorKind::InputError(format!(
                "Asteria Client {ASTERIA_CLIENT_VERSION} requires Fabric for Minecraft {ASTERIA_CLIENT_GAME_VERSION}",
            )),
        )
        .into());
    }

    let temp_path = std::env::temp_dir().join(format!(
        "asteria-client-{ASTERIA_CLIENT_VERSION}-{}.jar",
        std::process::id()
    ));
    tokio::fs::write(&temp_path, ASTERIA_CLIENT_JAR).await?;

    let install_result = theseus::instance::add_project_from_path(
        &instance_id,
        &temp_path,
        Some(ProjectType::Mod),
    )
    .await;
    let _ = tokio::fs::remove_file(&temp_path).await;

    Ok(install_result?)
}
