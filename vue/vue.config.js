module.exports = {
  devServer: {
    port: 8081,
    disableHostCheck: true
  },
  //   publicPath: "/mpb-static", //publicPath has to be in sync with nginx location path.
  publicPath: "/mpb/static", //publicPath has to be in sync with nginx location path.
  outputDir: undefined,
  assetsDir: undefined,
  runtimeCompiler: undefined,
  productionSourceMap: undefined,
  parallel: undefined,
  css: undefined
};
