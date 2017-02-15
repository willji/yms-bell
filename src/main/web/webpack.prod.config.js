var path = require('path');
var webpack = require('webpack');
var HtmlWebpackPlugin = require('html-webpack-plugin');

var config = {
  entry: {
    bundle: ['babel-polyfill', './app/index'],
    // vendor: ['react', 'react-dom', 'react-router']
  },
  output: {
    path: path.join(__dirname, '../resources/static'),
    filename: 'bundle-[chunkhash].js',
    publicPath: '/static/'
  },
  plugins: [
    // new webpack.optimize.CommonsChunkPlugin('vendor',  'vendor-[chunkhash].js', Infinity),
    new webpack.optimize.OccurenceOrderPlugin(),
    new webpack.optimize.UglifyJsPlugin({
      compress: {
        warnings: false
      }
    }),
    new webpack.DefinePlugin({
      'process.env': { NODE_ENV: JSON.stringify('production') }
    }),
    new HtmlWebpackPlugin({
      filename: path.join(__dirname, '../resources/index.html'),
      hash: false,
      inject: true,
      template: './index.tmpl.html'
    })
  ],
  module: {
    loaders: [
      {
        test: /\.js$/,
        loaders: ['babel'],
        exclude: /node_modules/,
        include: __dirname,
      },
      {
        test: /\.(css)$/,
        exclude: /\.(useable|post)\.(css|less)/,
        loader: 'style!css'
      },
      {
        test: /\.(less)$/,
        exclude: /\.(useable|post)\.(css|less)/,
        loader: 'style!css!less'
      },
      {
        test: /\.post\.css$/,
        loader: 'style-loader!css-loader?modules&importLoaders=1&localIdentName=[name]__[local]___[hash:base64:5]!postcss-loader'
      },
      {
        test: /\.useable\.(css)$/,
        loader: 'style/useable!css'
      },
      {
        test: /\.(woff|png|jpg|gif|svg)/,
        loader: 'file!url'
      },
      {
        test: /\.(ttf|eot|svg|woff(2)?)(\?[a-z0-9]+)?$/,
        loader: 'file-loader'
      }
    ]
  },
  postcss: [ require('autoprefixer')],

};


module.exports = config;
