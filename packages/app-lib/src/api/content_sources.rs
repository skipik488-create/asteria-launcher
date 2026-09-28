//! Content source capability discovery.

use serde::Serialize;

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
            install: curseforge_configured,
            requires_api_key: true,
            configured: curseforge_configured,
        },
    ]
}
