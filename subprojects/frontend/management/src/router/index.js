import Vue from 'vue';
import Router from 'vue-router';
import mainRouter from './main';

Vue.use(Router);

export default new Router({
  mode: 'history',
  base: process.env.BASE_URL,
  routes: [
    mainRouter,
    {
      path: '',
      children: [
        {
          path: '*',
          redirect: '/error-404',
        },
      ],
    },
    //   path: '/',
    //   name: 'index',
    //   components: {
    //     default: Index,
    //     header: MainNavbar,
    //     footer: MainFooter
    //   },
    //   props: {
    //     header: { colorOnScroll: 400 },
    //     footer: { backgroundColor: 'black' },
    //   },
    // },
    // {
    //   path: '/landing',
    //   name: 'landing',
    //   components: { default: Landing, header: MainNavbar, footer: MainFooter },
    //   props: {
    //     header: { colorOnScroll: 400 },
    //     footer: { backgroundColor: 'black' },
    //   },
    // },
    // {
    //   path: '/login',
    //   name: 'login',
    //   components: { default: Login, header: MainNavbar, footer: MainFooter },
    //   props: {
    //     header: { colorOnScroll: 400 },
    //   },
    // },
    // {
    //   path: '/profile',
    //   name: 'profile',
    //   components: { default: Profile, header: MainNavbar, footer: MainFooter },
    //   props: {
    //     header: { colorOnScroll: 400 },
    //     footer: { backgroundColor: 'black' },
    //   },
    // },
  ],
  scrollBehavior: to => {
    if (to.hash) {
      return { selector: to.hash };
    } else {
      return { x: 0, y: 0 };
    }
  },
});
