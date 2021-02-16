import path from 'path'
import nodeExternals from 'webpack-node-externals'

export default {
    mode: process.env.NODE_ENV || 'development',
    target: 'node',
    entry: {
        index: path.resolve(__dirname, './index.js'),
    },
    output: {
        filename: '[name].js',
        libraryTarget: 'commonjs',
    },
    module: {
        rules: [
            {
                test: /\.js$/,
                exclude: /node_modules/,
                use: [
                    {
                        loader: 'babel-loader',
                    },
                ],
            },
        ],
    },
    externals: [nodeExternals()],
}
