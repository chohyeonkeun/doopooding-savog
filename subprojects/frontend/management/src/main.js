import Vue from 'vue';
import './plugins/axios';
import MaterialKit from './plugins/material-kit';
import App from './App';
import router from 'router';
import Bluebird from 'bluebird';

Vue.config.productionTip = false;

Vue.use(MaterialKit);

window.Promise = Bluebird;

const NavbarStore = {
  showNavbar: false,
};

Vue.mixin({
  data() {
    return {
      NavbarStore,
    };
  },
});

/* eslint-disable no-new */
new Vue({
  router,
  render: h => h(App),
}).$mount('#app');
