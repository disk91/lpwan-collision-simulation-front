<template>
  <div>
    <h1>Frame Models</h1>
    <ul>
      <li v-for="frame in frameModels" :key="frame.id">
        Channel: {{ frame.channel }}, Group: {{ frame.group }}
      </li>
    </ul>
  </div>
</template>

<script setup>
import { useFetch } from '#app'

const config = useRuntimeConfig()
const { data: frameModels, error } = await useFetch(`${config.public.apiBase}/framemodels`)

if (error.value) {
  console.error(error.value)
}
</script>