import { find, isEmpty, startsWith } from 'lodash';
import Vue from 'vue';
import Router from 'vue-router';
import mainRouter from './main';

Vue.use(Router);

const router = new Router({
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

router.beforeEach((to, from, next) => {
  if (to.path === from.path) {
    next();
  } else if (startsWith(to.path, '/error')) {
    next();
  } else {
    const target = find(mainRouter.children, (route) => {
      const match = route.path.match(/\/(:([^/]+))(\/)*/g);
      if (isEmpty(match)) {
        return (route.path === to.path);
      } else {
        const replaced = route.path.replace(/\/(:([^/]+))(\/)*/g, '/.+');
        return to.path.match(new RegExp(replaced));
      }
    });
    if (isEmpty(target)) {
      next({ path: '/error-403' });
      return;
    }

    next();
  }
});

router.afterEach(() => {
  // Remove initial loading
  const appLoading = document.getElementById('loading-bg');
  if (appLoading) {
    appLoading.style.display = 'none';
  }
});

export default router;
