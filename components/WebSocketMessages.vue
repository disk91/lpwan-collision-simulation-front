<template>
    <div class="messages-container">
        <h2>Messages WebSocket</h2>
        <ul>
            <li v-for="(msg, index) in messages" :key="index" :class="msg.type">
                <strong v-if="msg.type === 'sent'">⬆️ Envoyé:</strong>
                <strong v-else>⬇️ Reçu:</strong>
                {{ msg.content }}
            </li>
        </ul>
    </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useWebSocketHandler } from '@/composables/useWebSocket';

const websocket = useWebSocketHandler();
const messages = ref([]);

watch(websocket.data, (newMessage) => {
    if (newMessage) {
        console.log("📥 Nouveau message reçu:", newMessage);
        messages.value.push({ type: 'received', content: newMessage });
    }
});

watch(websocket.lastMessage, (sentMessage) => {
    if (sentMessage) {
        console.log("📤 Message envoyé:", sentMessage);
        messages.value.push({ type: 'sent', content: sentMessage });
    }
});
</script>

<style scoped>
.messages-container {
    border: 1px solid #ccc;
    padding: 10px;
    margin-top: 20px;
    max-height: 200px;
    overflow-y: auto;
    background: #f9f9f9;
    border-radius: 5px;
}

ul {
    list-style: none;
    padding: 0;
}

li {
    padding: 5px;
    border-bottom: 1px solid #ddd;
}

li.sent {
    color: blue;
}

li.received {
    color: green;
}
</style>