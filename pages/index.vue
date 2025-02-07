<template>
  <SimulationMainControl/>
  <SpectrogramViewer/>
  <WebSocketMessages/>
</template>

<style scoped>
.container {
  text-align: center;
  padding: 20px;
}

p {
  font-size: 18px;
}

.messages {
  margin-top: 20px;
  text-align: left;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 5px;
}

.messages h2 {
  font-size: 18px;
}

.messages ul {
  list-style-type: none;
  padding: 0;
}

.messages li {
  padding: 5px;
  border-bottom: 1px solid #ddd;
}

.messages li:last-child {
  border-bottom: none;
}
</style>
