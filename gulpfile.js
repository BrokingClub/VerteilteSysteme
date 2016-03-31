var elixir = require('laravel-elixir');
elixir.config.assetsPath = 'src/main/resources/assets';
elixir.config.publicPath = 'src/main/public';

require('laravel-elixir-webpack');

elixir(function(mix) {
    mix.sass('app.scss');
    mix.webpack('app.js');
    mix.browserSync({
        proxy: 'localhost:8000',
        ui: false,
        ghostMode: false
    });
});