// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: '2024-11-01',
  devtools: { enabled: true },
  modules: [
    '@vueuse/nuxt',
    '@nuxt/ui'
  ],
  runtimeConfig: {
    public: {
      baseURL: process.env.API_BASE_URL || 'http://192.168.1.34:8080'
    }
  }
})
