<template>
    <nav class="navbar navbar-full navbar-dark bg-inverse">
        <a class="navbar-brand" v-link="{ path: '/', exact: true }">Woods Sooper Dooper Shop</a>
        <div class="nav navbar-nav">
            <a class="nav-item nav-link" v-link="{ path: '/', exact: true }">Produkte</a>
            <a class="nav-item nav-link" v-link="{ path: '/einkaufswagen' }" v-if="loggedIn">
                Einkaufswagen
                <span class="label label-pill label-default">{{ products }}</span>
            </a>
            <a class="nav-item nav-link" v-link="{ path: '/bestellungen' }" v-if="loggedIn">Bestellungen</a>
        </div>
        <div class="nav navbar-nav pull-sm-right">
            <a class="nav-item nav-link" v-link="{ path: '/anmelden' }" v-if="!loggedIn">Anmelden</a>
            <a class="nav-item nav-link" v-link="{ path: '/registrieren' }" v-if="!loggedIn">Registrieren</a>
            <a class="nav-item nav-link" v-if="loggedIn">{{ name }}</a>
            <a class="nav-item nav-link" v-link="{ path: '/backend' }" v-if="admin">Backend</a>
            <a class="nav-item nav-link" href="" v-if="loggedIn" @click="logout($event)">Abmelden</a>
        </div>
    </nav>
</template>

<script type="text/babel">
    import userService from '../services/userService';
    import shoppingCartService from '../services/shoppingCartService';

    export default {
        data() {
            return {
                userService,
                shoppingCartService
            };
        },
        computed: {
            products() {
                return this.shoppingCartService.products.length
            },
            loggedIn() {
                return userService.user !== null;
            },
            admin() {
                const user = userService.user;

                if(user) {
                    return user.admin;
                }

                return false;
            },
            name() {
                const user = userService.user;

                if(user) {
                    return user.name;
                }

                return '';
            }
        },
        methods: {
            logout(event) {
                event.preventDefault();
                toastr.info('Bis bald ' + this.userService.user.name, 'Abgemeldet');
                userService.logout();
                this.$route.router.go('/');
            }
        }
    };
</script>