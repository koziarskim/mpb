module.exports = {
  devServer: {
    port: 8081,
    disableHostCheck: true,
    public: "0.0.0.0" //This is hack for sockjs picking wrong path. "marcin.noovitec.com" also works.
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
