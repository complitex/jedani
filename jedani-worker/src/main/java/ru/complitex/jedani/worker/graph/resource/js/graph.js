var cy;

document.addEventListener("DOMContentLoaded", function(event) {
    cy = cytoscape({
        container: document.getElementById('cy'),
        elements: [
            { data: { id: 'a' } },
            { data: { id: 'b' } },
            {
                data: {
                    id: 'ab',
                    source: 'a',
                    target: 'b'
                }
            }],
        style: [
            {
                selector: 'node',
                style: {
                    label: 'data(id)'
                }
            }]
    });
});



