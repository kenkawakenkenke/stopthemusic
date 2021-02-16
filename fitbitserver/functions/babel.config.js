module.exports = api => {
    api.cache(true)

    return {
        presets: [
            ['@babel/preset-env', {
                useBuiltIns: 'usage',
                corejs: 3,
                targets: {
                    node: '8',
                },
            }],
        ],
        overrides: [
            {
                test: './*.babel.js',
                presets: [
                    ['@babel/preset-env', {
                        targets: {
                            node: 'current',
                        },
                    }],
                ],
            },
        ],
    }
}