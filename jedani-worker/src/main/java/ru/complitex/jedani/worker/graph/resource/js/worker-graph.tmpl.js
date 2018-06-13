var cy = window.cy = cytoscape({
    container: document.getElementById('cy'),

    layout: {
        name: 'cose-bilkent',
        animate: false,
        randomize: true
    },

    style: [
        {
            selector: 'node',
            style: {
                // 'background-color': '#ad1a66'
                'label': 'data(label)',
                'font-size': '10px'
            }
        },

        {
            selector: 'edge',
            style: {
                // 'width': 3,
                // 'line-color': '#ad1a66'
            }
        }
    ],

    elements: [${elements}]
});