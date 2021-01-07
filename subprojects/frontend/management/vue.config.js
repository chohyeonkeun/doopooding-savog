// const fs = require('fs');
const path = require('path');

const srcRoot = './src';
const targetHost = 'localhost';
const targetPort = '8080';

const port = 8000;
let cookie;

const replaceCookie = function(cookie) {
  return cookie
      .replace(/\s+domain=[^\s;]+;?/, '')
      .replace(/\s+secure;?/, '')
};

module.exports = {
  // test
  devServer: {
    port: port,
    clientLogLevel: 'debug',
    historyApiFallback: true,
    hot: true,
    inline: true,
    publicPath: `http://localhost:${port}`,
    transportMode: 'sockjs',
    headers: {
      'Access-Controll-Allow-Origin': '*'
    },
    proxy: {
      '/^/ws': {
        target: 'http://${targetHost}:${targetPort}',
        ws: true,
        changeOrigin: true,
        onProxyReq: (proxyReq) => {
          if (proxyReq.getHeader('origin')) {
            proxyReq.setHeader('origin', `http://${targetHost}:${targetPort}`);
          }

          if (proxyReq.getHeader('set-cookie')) {
            proxyReq.headers['set-cookie'] = proxyReq.headers['set-cookie'].map(replaceCookie)
          }

          if (cookie) proxyReq.setHeader('Cookie', cookie);
        },
        onProxyRes: (proxyRes) => {
          if (proxyRes.headers['set-cookie']) {
            cookie = proxyRes.headers['set-cookie'] = proxyRes.headers['set-cookie'].map(replaceCookie)
          }
        },
      },
      '^/(sse|api|sign|resources)': {
        target: `http://${targetHost}:${targetPort}`,
        changeOrigin: true,
        onProxyRes: (proxyRes) => {
          if (proxyRes.headers['set-cookie']) {
            cookie = proxyRes.headers['set-cookie'] = proxyRes.headers['set-cookie'].map(replaceCookie)
          }
        },
      }
    }
  },

  pluginOptions: {
    moment: {
      locales: ['ko_kr']
    }
  },
  // sample
  // lintOnSave: process.env.NODE_ENV === 'development',
  // publicPath: '/',
  // chainWebpack: (config) => {
  //   config.plugin('define').tap((definitions) => {
  //     definitions[0].__DEV__ = process.env.NODE_ENV === 'development';
  //     return definitions;
  //   });

  //   // add `src/*` to alias
  //   const files = fs.readdirSync(`${__dirname}/${srcRoot}`);
  //   files.filter((file) => fs.statSync(`${srcRoot}/${file}`).isDirectory())
  //     .forEach((file) => {
  //       config.resolve.alias.set(file, path.resolve(__dirname, `${srcRoot}/${file}`));
  //     });
  // },
  // 모든 컴포넌트에 css 적용
  // css: {
  //   loaderOptions: {
  //     sass: {
  //       prependData: `@import '@/assets/scss/common.scss';`
  //     }
  //   }
  // }

  css: {
    loaderOptions: {
      css: {
        sourceMap: process.env.NODE_ENV !== "production" ? true : false
      }
    }
  }
};
