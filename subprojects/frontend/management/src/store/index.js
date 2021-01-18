import Vue from 'vue';
import Vuex from 'vuex';
import VuexPersist from 'vuex-persist';
import moduleUser from 'store/modules/user';

Vue.use(Vuex);

const vuexPersist = new VuexPersist({
  key: 'jonus-savog',
  storage: localStorage,
});

export default new Vuex.Store({
  modules: {
    user: moduleUser,
  },
  strict: process.env.NODE_ENV !== 'production',
  plugins: [vuexPersist.plugin],
});
