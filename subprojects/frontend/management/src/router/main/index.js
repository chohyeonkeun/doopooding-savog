import Home from 'views';
import MainNavbar from 'layout/MainNavbar.vue';
import MainFooter from 'layout/MainFooter.vue';
import Pet from 'views/pet';

const router = {
  path: '',
  children: [
    {
      path: '/',
      name: 'home',
      components: {
        default: Home,
        header: MainNavbar,
        footer: MainFooter,
      },
      props: {
        header: { colorOnScroll: 400 },
        footer: { backgroundColor: 'black' },
      },
    },
    {
      path: '/pet',
      name: 'pet',
      components: {
        default: Pet,
        header: MainNavbar,
        footer: MainFooter,
      },
      props: {
        header: { colorOnScroll: 400 },
        footer: { backgroundColor: 'black' },
      },
      // meta: {
      //   breadcrumb: [
      //     { title: 'Home', url: '/' },
      //     { title: '애완동물', active: true },
      //   ],
      //   pageTitle: '애완동물',
      // },
    },
  ],
};

export default router;