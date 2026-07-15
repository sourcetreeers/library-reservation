module.exports = {
  devServer: {
    port: 3000,
    historyApiFallback: true,
    client: {
      overlay: false
    },
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/admin': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/system-admin': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/mobile': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/uploads': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },
  publicPath: './',
  outputDir: 'dist',
  assetsDir: 'static'
}