use crate::api::Result;
use tauri::Runtime;
use theseus::content_sources::ContentSourceCapability;

pub fn init<R: Runtime>() -> tauri::plugin::TauriPlugin<R> {
    tauri::plugin::Builder::new("content_sources")
        .invoke_handler(tauri::generate_handler![capabilities])
        .build()
}

#[tauri::command]
pub async fn capabilities() -> Result<Vec<ContentSourceCapability>> {
    Ok(theseus::content_sources::capabilities())
}
