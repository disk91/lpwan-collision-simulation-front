<template>
    <div class="layout-container">
      <!-- Barre supérieure -->
      <TopBar />
  
      <div class="main-content">
        <!-- Sidebar toujours présente sur grand écran, ou gérée via un menu hamburger sur mobile -->
        <SideBar />
        
        <!-- Contenu de la page (ici pages/index.vue) -->
        <div class="page-content">
          <NuxtPage />
        </div>
      </div>
    </div>
  </template>
  
  <script setup lang="ts">
  import TopBar from '~/components/TopBar.vue'
  import SideBar from '~/components/SideBar.vue'
  </script>
  
  <style scoped>
  .layout-container {
    display: flex;
    flex-direction: column;
    height: 100vh;
  }
  
  .main-content {
    display: flex;
    flex: 1;
    overflow: hidden;
  }
  
  .page-content {
    flex: 1;
    overflow-y: auto;
  }
  </style>
  