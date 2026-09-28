import { invoke } from '@tauri-apps/api/core'

export type ContentSource = 'modrinth' | 'curseforge'

export type ContentSourceCapability = {
	source: ContentSource
	search: boolean
	install: boolean
	requires_api_key: boolean
	configured: boolean
}

export async function getContentSourceCapabilities(): Promise<ContentSourceCapability[]> {
	return await invoke('plugin:content_sources|capabilities')
}
