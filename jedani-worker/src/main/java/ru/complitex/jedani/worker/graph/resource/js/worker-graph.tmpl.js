var fileName = window.fileName = "${fileName}";

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
                'background-color': '#fff',
                'border-color': '#808080',
                'border-width': '1px',
                'width': '100px',
                'height': '100px',
                'padding' : '3px',
                'label': 'data(label)',
                'font-size': '13px',
                'font-family': 'Arial, Helvetica, sans-serif',
                'font-weight': 'normal',
                'text-wrap': 'wrap',
                'text-valign': 'center',
                'overlay-color': '#fff',
                'overlay-opacity': '0'
            }
        },

        {
            selector: 'edge',
            style: {
                'width': '1px',
                'curve-style': 'bezier',
                'line-color': '#808080',
                'target-arrow-shape': 'triangle',
                'target-arrow-fill': 'filled',
                'target-arrow-color': '#808080'
            }
        },

        {
            selector: 'core',
            style: {
                'active-bg-color': '#fff',
                'selection-box-color': '#fff'
            }
        }
    ],

    wheelSensitivity: 0.01,

    elements: [${elements}]
});

cy.zoom(1.1);
cy.center();