module.exports = {
  devServer: {
    port: 8081,
    disableHostCheck: true,
    sockHost: "marcin.noovitec.com/mpb-static"
  },
  //publicPath has to be in sync with nginx location path. This is defined in ".env...."
  publicPath: process.env.VUE_APP_STATIC_BASE_PATH,
  outputDir: undefined,
  assetsDir: undefined,
  runtimeCompiler: undefined,
  productionSourceMap: undefined,
  parallel: undefined,
  css: undefined
};
