import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  base:process.env.VITE_BASE_PATH || "/audio_streaming_app/tree/main/audio-streaming-app/audio-streaming-app"
})
