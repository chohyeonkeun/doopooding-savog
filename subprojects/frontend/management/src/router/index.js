import Vue from 'vue';
import Router from 'vue-router';
import Home from 'views/Index';
import Sample from 'views/sample';
import MainNavbar from 'layout/MainNavbar.vue';
import MainFooter from 'layout/MainFooter.vue';
import Pet from 'views/pet';
import SponsorshipFee from 'views/sponsorshipFee';
import Login from 'views/Login';

Vue.use(Router);

const router = new Router({
  mode: 'history',
  base: process.env.BASE_URL,
  routes: [
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
      path: '/sample',
      name: 'sample',
      components: {
        default: Sample,
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
      meta: {
        breadcrumb: [
          { title: 'Home', url: '/' },
          { title: '애완동물', active: true },
        ],
        pageTitle: '애완동물',
      },
    },
    {
      path: '/sponsorshipFee',
      name: 'sponsorshipFee',
      components: {
        default: SponsorshipFee,
        header: MainNavbar,
        footer: MainFooter,
      },
      props: {
        header: { colorOnScroll: 400 },
        footer: { backgroundColor: 'black' },
      },
      meta: {
        breadcrumb: [
          { title: 'Home', url: '/' },
          { title: '후원금', active: true },
        ],
        pageTitle: '후원금',
      },
    },
    {
      path: '/login',
      name: 'login',
      components: { default: Login, header: MainNavbar, footer: MainFooter },
      props: {
        header: { colorOnScroll: 400 },
      },
    },
    {
      path: '',
      children: [
        {
          path: '*',
          redirect: '/error-404',
        },
      ],
    },
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
    //   path: '/profile',
    //   name: 'profile',
    //   components: { default: Profile, header: MainNavbar, footer: MainFooter },
    //   props: {
    //     header: { colorOnScroll: 400 },
    //     footer: { backgroundColor: 'black' },
    //   },
    // },
  ],
});

export default router;
