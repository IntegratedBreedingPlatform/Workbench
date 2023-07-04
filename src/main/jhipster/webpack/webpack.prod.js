const webpack = require('webpack');
const webpackMerge = require('webpack-merge');
// const TerserPlugin = require('terser-webpack-plugin');
const WorkboxPlugin = require('workbox-webpack-plugin');
const AngularCompilerPlugin = require('@ngtools/webpack').AngularCompilerPlugin;
const MiniCssExtractPlugin = require('mini-css-extract-plugin');

const path = require('path');
const utils = require('./utils.js');
const commonConfig = require('./webpack.common.js');

const ENV = 'production';

module.exports = webpackMerge(commonConfig({ env: ENV }), {
    // Enable source maps. Please note that this will slow down the build.
    // You have to enable it in UglifyJSPlugin config below and in tsconfig-aot.json as well
    // devtool: 'source-map',
    entry: {
        polyfills: './src/main/webapp/app/polyfills',
        global: './src/main/webapp/content/scss/global.scss',
        main: './src/main/webapp/app/app.main'
    },
    output: {
        path: utils.root('../../../src/main/webapp/WEB-INF/pages/jhipster'),
        filename: 'app/[name].[hash].bundle.js',
        chunkFilename: 'app/[id].[hash].chunk.js'
    },
    module: {
        rules: [{
            test: /(?:\.ngfactory\.js|\.ngstyle\.js|\.ts)$/,
            use: [ '@ngtools/webpack' ]
        },
            {
                test: /\.scss$/,
                loaders: ['to-string-loader', 'css-loader', 'sass-loader'],
                exclude: /(vendor\.scss|global\.scss)/
            },
            {
                test: /(vendor\.scss|global\.scss)/,
                use: [{
                    loader: MiniCssExtractPlugin.loader,
                    options: {
                        publicPath: '../',
                    },
                },
                'css-loader',
                'postcss-loader',
                'sass-loader']
            },
        {
            test: /\.css$/,
            loaders: ['to-string-loader', 'css-loader']
        }]
    },
    optimization: {
        runtimeChunk: false,
        splitChunks: {
            cacheGroups: {
                commons: {
                    test: /[\\/]node_modules[\\/]/,
                    name: 'vendors',
                    chunks: 'all'
                }
            }
        },
        minimizer: [
        	/* TODO Review IBP-4719, potential performance bottleneck
            new TerserPlugin({
            parallel: true,
                cache: true,
                terserOptions: {
                ie8: false,
                // sourceMap: true, // Enable source maps. Please note that this will slow down the build
                compress: {
                    dead_code: true,
                    warnings: false,
                    properties: true,
                    drop_debugger: true,
                    conditionals: true,
                    booleans: true,
                    loops: true,
                    unused: true,
                    toplevel: true,
                    if_return: true,
                    inline: true,
                    join_vars: true
                },
                output: {
                    comments: false,
                    beautify: false,
                    indent_level: 2,
                    ascii_only: true
                }
            }
            })
        	 */
        ]
    },
    plugins: [
        new MiniCssExtractPlugin({
            filename: `content/[name]-sass.[hash].css`
        }),
        new AngularCompilerPlugin({
            mainPath: utils.root('src/main/webapp/app/app.main.ts'),
            tsConfigPath: utils.root('tsconfig-aot.json'),
            sourceMap: true
        }),
        new webpack.LoaderOptionsPlugin({
            minimize: true,
            debug: false
        }),
        new WorkboxPlugin.GenerateSW({
          clientsClaim: true,
          skipWaiting: true,
        })
    ],
    // TODO continue IBP-3492
    performance: {
        //values expressed in bytes
        maxAssetSize: 4000000,
        maxEntrypointSize: 4000000,
        hints: "warning"
    },
    mode: 'production'
});
