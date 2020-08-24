// vue.config.js
const path = require('path');

const resolve = (dir) => path.join(__dirname, dir)

module.exports = {
	chainWebpack: config => {
		//路径配置
		config.resolve.alias
			.set('assets', resolve('src/assets'))
			.set('styles', resolve('src/assets/styles'));
			
		config.module
			.rule("css")
			.test(/\.css$/)
			.oneOf("vue")
			.resourceQuery(/\?vue/)
			.use("px2rem")
			.loader("px2rem-loader")
			.options({
				remUnit: 192
			});
	},

	// webpack-dev-server 相关配置  
	devServer: {
		// 调试端口
		// port: 8989
	}
	//其他配置....
}
