module.exports = {
  devServer: {
    port: 8081,
    disableHostCheck: true
  },
  publicPath: process.env.VUE_APP_STATIC_BASE_PATH, //publicPath has to be in sync with nginx location path.
  outputDir: undefined,
  assetsDir: undefined,
  runtimeCompiler: undefined,
  productionSourceMap: undefined,
  parallel: undefined,
  css: undefined
};
