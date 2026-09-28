use crate::api::Result;
use tauri::Runtime;
use theseus::content_sources::{
    ContentSourceCapability, CurseForgeSearchResults,
};

pub fn init<R: Runtime>() -> tauri::plugin::TauriPlugin<R> {
    tauri::plugin::Builder::new("content_sources")
        .invoke_handler(tauri::generate_handler![
            capabilities,
            search_curseforge
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
