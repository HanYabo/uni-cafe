import { createSSRApp } from "vue";
import App from "./App.vue";
// 启用pinia状态管理
import { createPinia } from "pinia";
export function createApp() {
	const app = createSSRApp(App);
	app.use(createPinia());
	
	return {
		app,
	};
}
