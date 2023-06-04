function drawCanvas() {
    //Triangle
    context.beginPath();
    context.fillStyle = "#004cff";
    context.strokeStyle = "#004cff";
    context.font = '20px serif';
    context.moveTo(150, 150);
    context.lineTo(200, 150);
    context.lineTo(150, 250);
    context.fill();
    //Rect
    context.rect(150, 150, 50, -100);
    context.fill();
    //Circle
    context.beginPath();
    context.arc(150, 150, 50, 0.5 * Math.PI, Math.PI);
    context.lineTo(150, 150);
    context.fill();
    context.closePath();
    context.stroke();
    //Horizontal line
    context.beginPath();
    context.strokeStyle = "rgb(0,0,0)";
    context.lineWidth = 2;
    context.moveTo(0, 150);
    context.lineTo(290, 150);
    //Vertical line
    context.moveTo(150, 10);
    context.lineTo(150, 300);
    //Serifs
    context.moveTo(50, 140);
    context.lineTo(50, 160);
    context.moveTo(100, 140);
    context.lineTo(100, 160);
    context.moveTo(200, 140);
    context.lineTo(200, 160);
    context.moveTo(250, 140);
    context.lineTo(250, 160);
    context.moveTo(140, 50);
    context.lineTo(160, 50);
    context.moveTo(140, 100);
    context.lineTo(160, 100);
    context.moveTo(140, 200);
    context.lineTo(160, 200);
    context.moveTo(140, 250);
    context.lineTo(160, 250);
    context.stroke();
    //Arrows
    context.fillStyle = "#000000";
    context.moveTo(150, 0);
    context.lineTo(145, 15);
    context.lineTo(155, 15);
    context.closePath();

    context.moveTo(300, 150);
    context.lineTo(285, 145);
    context.lineTo(285, 155);
    context.closePath();
    context.fillText('-R', 45, 180);
    context.fillText('-R/2', 95, 180);
    context.fillText('R/2', 195, 180);
    context.fillText('R', 245, 180);
    context.fillText('R', 170, 55);
    context.fillText('R/2', 170, 105);
    context.fillText('-R/2', 170, 205);
    context.fillText('-R', 170, 255);
    context.fill();
    context.stroke();
}

function clearCanvas() {
    context.clearRect(0, 0, 300, 300);
}

function putDot(x, y) {
    context.beginPath();
    context.fillStyle = "#fd0000";
    context.rect(x, y, 3, 3);
    context.fill();
    context.closePath();
    changeInputY(y);
    getSelectedX(x);
}

function drawTablePoint(x, y, r, hitResult) {
    if (Number(r) === Number(rValue) || Number(r) === (Number(rValue) - 0.1)) {
        context.fillStyle = hitResult === 'Промах' ? 'red' : 'green';
        context.beginPath();
        context.rect(x / r * 100 + (300 / 2), -y / r * 100 + (300 / 2), 3, 3);
        context.fill();
    }
}

function loadTablePoints() {
    let rows = [];
    let headers = $(".result_table_points th");

    $(".result_table_points tr").each(function(index) {
        let cells = $(this).find("td");
        rows[index] = {};
        cells.each(function(cellIndex) {
            rows[index][$(headers[cellIndex]).html()] = $(this).html().replace(/\s/g, "");
        });
    });

    for (let i = 0; i < rows.length; i++) {
        drawTablePoint(
            rows[i]['X'],
            rows[i]['Y'],
            rows[i]['R'],
            rows[i]['Результат']);
    }
}