import { invoke } from '@tauri-apps/api/core'

export type ContentSource = 'modrinth' | 'curseforge'

export type ContentSourceCapability = {
	source: ContentSource
	search: boolean
	install: boolean
	requires_api_key: boolean
	configured: boolean
}

export type CurseForgeProject = {
	id: number
	name: string
	slug: string
	summary: string
	downloadCount: number
	dateCreated: string
	dateModified: string
	authors: { name: string }[]
	categories: { name: string }[]
	logo?: { thumbnailUrl?: string; url?: string }
	latestFilesIndexes: {
		gameVersion: string
		fileId: number
		modLoader?: number
	}[]
}

export type CurseForgeSearchResults = {
	projects: CurseForgeProject[]
	index: number
	page_size: number
	total_count: number
}

export async function getContentSourceCapabilities(): Promise<ContentSourceCapability[]> {
	return await invoke('plugin:content_sources|capabilities')
}

export async function searchCurseForge(input: {
	query: string
	projectType: string
	gameVersion?: string
	loader?: string
	index: number
	pageSize: number
}): Promise<CurseForgeSearchResults> {
	return await invoke('plugin:content_sources|search_curseforge', {
		query: input.query,
		projectType: input.projectType,
		gameVersion: input.gameVersion,
		loader: input.loader,
		index: input.index,
		pageSize: input.pageSize,
	})
}
