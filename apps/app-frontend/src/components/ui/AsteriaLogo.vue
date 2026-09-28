<script setup lang="ts">
withDefaults(
	defineProps<{
		wordmark?: boolean
	}>(),
	{
		wordmark: false,
	},
)
</script>

<template>
	<div class="inline-flex items-center gap-2" aria-label="Asteria Launcher">
		<svg viewBox="0 0 64 64" class="h-full w-auto shrink-0" aria-hidden="true">
			<rect width="64" height="64" rx="14" fill="#151927" />
			<path d="M12.5 45.25 29.4 11.5l5.25 2.65L17.75 48.1Z" fill="#74A7FF" />
			<path d="m29.4 14.15 5.25-2.65 16.9 33.75-5.25 2.85Z" fill="#74A7FF" />
			<rect x="20.75" y="33.5" width="22.5" height="4.5" rx="2.25" fill="#FFFFFF" />
			<circle cx="32" cy="13.4" r="2.6" fill="#FFFFFF" />
			<circle cx="15.25" cy="46.75" r="2.1" fill="#A98BFF" />
			<circle cx="48.75" cy="46.75" r="2.1" fill="#A98BFF" />
		</svg>
		<span v-if="wordmark" class="font-semibold tracking-tight text-contrast">Asteria</span>
	</div>
</template>
