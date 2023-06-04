let coordinateX;
let coordinateY;
let yValue = $('.y_value').val();
let rValue = $('.r_value').val();
let canvas = document.getElementById("canvas");
let context = canvas.getContext("2d");
let isDotPut = false;

$(document).ready(function (){
    drawCanvas();
    loadTablePoints();
})

document.getElementById("canvas").addEventListener('click', ev => {
    document.querySelector('.validation_message').innerHTML = "";
    if (checkOnSelectedR()) {
        coordinateX = ev.offsetX;
        coordinateY = ev.offsetY;
        clearCanvas();
        drawCanvas();
        loadTablePoints();
        putDot(coordinateX, coordinateY);
        isDotPut = true;
    }
})

$("#submit").on("submit", function (event) {
    isDotPut = false;
})

function updateR() {
    rValue = $('.r_value').val();
    yValue = $('.y_value').val();
    if (yValue !== "" && isDotPut) {
        changeInputY(coordinateY);
    }
    getSelectedX(coordinateX);
    clearCanvas();
    drawCanvas();
    loadTablePoints();
}
