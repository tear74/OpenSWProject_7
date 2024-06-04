<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">

<head>
  <meta charset="UTF-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Document</title>
  <link rel="stylesheet" href="./main.css">
  <link rel="stylesheet" href="./snow.css">
  <link rel="stylesheet" href="./rain.css">

  <link
    href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR&family=Poppins:ital,wght@0,500;1,300&family=Roboto:wght@500&display=swap"
    rel="stylesheet">
</head>

<body>
  <!-- Title -->
  <div id="title">
    <a href="#">오늘 날씨는?</a>
  </div>
  <!-- Title -->

  <!-- <button class="btn" id="snow_btn" onclick="fallingsnow(),snowy_background()">Snow</button>
  <button class="btn" id="rain_btn" onclick="rain()">Rain</button> -->
  <div>

    <!-- search_bar-->
    <div class="search-container">
      <input id="search" type="text" placeholder="위치를 입력하세요">
      <button type="submit" id="submit"></button>
    </div>
    <!-- search_bar-->

  </div>
  <div id='container'>
    <div id="icon">
    </div>
    <div id="main">
      <img id="weather_icon">

      <h1 id="temp">
      </h1>
      <h2 id="city">
      </h2>
      <h3 id="disc">
      </h3>
    </div>
  </div>
  <script src="js/korean.js"></script>                    <!--weatherAPI 날씨정보 한글로 바꾸기 및 날씨에 맞는 아이콘 지정-->
  <script src="js/snow.js"></script>                      <!--눈내리는 효과-->
  <script src="js/rain.js"></script>                      <!--비내리는 효과-->
  <script src="js/backgroundcolor.js"></script>           <!--시간 및 날씨에 따른 배경 색 지정-->
  
  <script>
    var canvas = document.getElementById('container');

    //검색 관련 
    var input = document.getElementById('search');
    var submit = document.getElementById('submit');

    //온도 및 도시이름
    var temp = document.getElementById('temp');
    var city = document.getElementById('city');
    var desc = document.getElementById('disc');

    //날씨 아이콘
    var weather_icon = document.getElementById('weather_icon');

    // 검색 버튼 클릭시 날씨 정보를 받아옴
    submit.addEventListener('click', function () {
      fetch('https://api.openweathermap.org/data/2.5/weather?q=' + input.value +
          '&appid=6a24018e70842873eb6e29f47e203af0')
        .then(Response => Response.json()) //json 형태로 변환
        // data 처리
        .then(data => {
          console.log(data)
          var cityName = data['name'];
          var temperature = (data['main']['temp'] - 273.15).toFixed(1) + "°C";
          var descVal = wDescEngToKor(data['weather'][0]['id'])

          // 컨테이너에 온도와 도시 이름 출력 
          temp.innerHTML = temperature;
          city.innerHTML = cityName;
          desc.innerHTML = descVal;

          console.log(cityName, temperature);
          console.log(wDescEngToKor(data['weather'][0]['id']));
          weather_icon.src = (weatherIcon(data['weather'][0]['id']));
        })
        .catch(err => alert("지역명이 잘못되었습니다!"))
    })

    // enter키 입력
    input.addEventListener('keypress', function (e) {
      if (e.keyCode === 13) {
        e.preventDefault(); 
        submit.click(); 
      }
    });
  </script>
</body>

</html>