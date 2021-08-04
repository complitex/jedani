let options = {
    chart: {
        locales: [{
            "name": "ru",
            "options": {
                "months": ["Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"],
                "shortMonths": ["Янв", "Фев", "Мар", "Апр", "Май", "Июн", "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек"],
                "days": ["Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"],
                "shortDays": ["Вс", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб"],
                "toolbar": {
                    "exportToSVG": "Сохранить SVG",
                    "exportToPNG": "Сохранить PNG",
                    "menu": "Menu",
                    "selection": "Выбор",
                    "selectionZoom": "Выбор с увеличением",
                    "zoomIn": "Увеличить",
                    "zoomOut": "Уменьшить",
                    "pan": "Перемещение",
                    "reset": "Сбросить увеличение"
                }
            }
        }],
        defaultLocale: 'ru',
        type: 'line',
        height: 250,
        animations: {
            enabled: false
        },
        toolbar: {
            show: true,
            tools: {
                download: false,
                selection: false,
                zoom: false,
                zoomin: true,
                zoomout: true,
                pan: false,
                reset: false
            }
        },
    },
    series: [{
        name: 'Курс',
        data: [${data}]
    }],
    xaxis: {
        type: 'datetime',
        labels: {
            style: {
                color: '#333',
                fontSize: '13px',
                fontFamily: 'Arial, Helvetica, sans-serif'
            }
        },
        tooltip: {
            enabled: false
        },
        axisBorder:{
            color: '#ddd'
        },
        axisTicks: {
            color: '#ddd'
        },
        crosshairs: {
            show: false
        }
    },
    yaxis: {
        labels: {
            style: {
                color: '#333',
                fontSize: '13px',
                fontFamily: 'Arial, Helvetica, sans-serif'
            },
            formatter: function (value) {
                return value;
            }
        },
        axisBorder:{
            color: '#ddd'
        },
        axisTicks: {
            color: '#ddd'
        }
    },
    colors: ['#15c'],
    stroke: {
        width: 2
    }
};

const chart = new ApexCharts(document.querySelector("#chart"), options);

chart.render();
