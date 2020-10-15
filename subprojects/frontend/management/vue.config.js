const fs = require('fs');
const path = require('path');

const srcRoot = './src';
module.exports = {
  lintOnSave: process.env.NODE_ENV === 'development',
  publicPath: '/',
  chainWebpack: (config) => {
    config.plugin('define').tap((definitions) => {
      definitions[0].__DEV__ = process.env.NODE_ENV === 'development';
      return definitions;
    });

    // add `src/*` to alias
    const files = fs.readdirSync(`${__dirname}/${srcRoot}`);
    files.filter((file) => fs.statSync(`${srcRoot}/${file}`).isDirectory())
      .forEach((file) => {
        config.resolve.alias.set(file, path.resolve(__dirname, `${srcRoot}/${file}`));
      });
  },
  // 모든 컴포넌트에 css 적용
  // css: {
  //   loaderOptions: {
  //     sass: {
  //       prependData: `@import '@/assets/scss/common.scss';`
  //     }
  //   }
  // }
};
