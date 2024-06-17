import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WeatherApp extends JFrame {
    private static final String API_KEY = "6a24018e70842873eb6e29f47e203af0";
    private static final String[] CITIES = {"Seoul", "Asan", "Incheon", "Cheonan", "Daejeon"};
    private static final Color[] PANEL_COLORS = {new Color(255, 228, 225), new Color(240, 255, 240),
            new Color(230, 230, 250), new Color(255, 250, 205), new Color(240, 255, 255)};
    private Image backgroundImage;

    private HashMap<String, ArrayList<String>> bulletinBoards = new HashMap<>();
    private HashMap<String, DefaultListModel<String>> listModels = new HashMap<>();

    public WeatherApp() {
        setTitle("오늘 날씨는?");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout(100, 50, 50));
        setBackground(new Color(173, 216, 230));

        try {
            backgroundImage = ImageIO.read(new File("images/background.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        setContentPane(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        });

        for (int i = 0; i < CITIES.length; i++) {
            String city = CITIES[i];
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout(50, 50));
            panel.setBorder(new LineBorder(Color.BLACK, 2));
            panel.setBackground(PANEL_COLORS[i]);
            panel.setBorder(new EmptyBorder(10, 10, 10, 10));
            panel.setPreferredSize(new Dimension(300, 150));

            bulletinBoards.put(city, new ArrayList<>());
            listModels.put(city, new DefaultListModel<>());

            JLabel cityLabel = new JLabel("City: ");
            cityLabel.setFont(new Font("Serif", Font.BOLD, 16));
            JLabel tempLabel = new JLabel("Temperature: ");
            tempLabel.setFont(new Font("Serif", Font.PLAIN, 14));
            JLabel descLabel = new JLabel("Description: ");
            descLabel.setFont(new Font("Serif", Font.PLAIN, 14));
            JLabel iconLabel = new JLabel();

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setOpaque(false);
            textPanel.add(cityLabel);
            textPanel.add(tempLabel);
            textPanel.add(descLabel);

            panel.add(textPanel, BorderLayout.CENTER);
            panel.add(iconLabel, BorderLayout.EAST);

            add(panel);

            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    new Thread(() -> {
                        try {
                            String weatherData = getWeatherData(city);
                            JSONObject weatherJson = new JSONObject(weatherData);
                            showDetailedInfo(weatherJson);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }).start();
                }
            });

            new Thread(() -> {
                try {
                    String weatherData = getWeatherData(city);
                    JSONObject weatherJson = new JSONObject(weatherData);
                    String cityName = weatherJson.getJSONObject("city").getString("name");
                    String temperature = kelvinToCelsius(weatherJson.getJSONArray("list").getJSONObject(0).getJSONObject("main").getDouble("temp"));
                    int weatherId = weatherJson.getJSONArray("list").getJSONObject(0).getJSONArray("weather").getJSONObject(0).getInt("id");
                    String description = wDescEngToKor(weatherId);
                    String iconCode = weatherJson.getJSONArray("list").getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("icon");
                    String iconUrl = weatherIcon(iconCode);

                    // Update UI
                    SwingUtilities.invokeLater(() -> {
                        cityLabel.setText("City: " + cityName);
                        tempLabel.setText("Temperature: " + temperature);
                        descLabel.setText("Description: " + description);
                        try {
                            iconLabel.setIcon(new ImageIcon(new URL(iconUrl)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        cityLabel.setText("Error fetching data for " + city + ": " + e.getMessage());
                    });
                }
            }).start();
        }

        // Schedule bulletin board reset every 12 hours
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                resetBulletinBoards();
            }
        }, 0, 12 * 60 * 60 * 1000); // 12 hours in milliseconds
    }

    private void showDetailedInfo(JSONObject data) {
        // JSON 구조를 출력하여 디버깅
        System.out.println(data.toString(2));

        String cityName = data.getJSONObject("city").getString("name");
        JSONArray weatherList = data.optJSONArray("list");

        // weatherList가 null인 경우를 처리
        if (weatherList == null) {
            JOptionPane.showMessageDialog(null, "선택한 도시에 대한 날씨 데이터가 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 각 시간대의 날씨 데이터를 저장할 맵 생성
        Map<String, JSONObject> weatherMap = new HashMap<>();
        List<String> timeList = new ArrayList<>();

        for (int i = 0; i < weatherList.length(); i++) {
            JSONObject weatherData = weatherList.getJSONObject(i);
            String utcTime = weatherData.optString("dt_txt"); // 시간 정보가 "dt_txt"에 있다고 가정
            String localTime = convertUtcToLocalTime(utcTime); // UTC를 로컬 시간으로 변환
            weatherMap.put(localTime, weatherData);
            timeList.add(localTime);
        }

        // 다이얼로그 생성
        JDialog dialog = new JDialog((Frame) null, cityName + " - 상세 날씨 정보", true);
        dialog.setSize(400, 500);
        dialog.setLayout(new BorderLayout(10, 10));

        // 날씨 정보를 표시할 패널 생성
        JPanel weatherPanel = new JPanel();
        weatherPanel.setLayout(new GridLayout(6, 1));
        weatherPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        weatherPanel.setBackground(Color.WHITE);

        JLabel tempLabel = new JLabel();
        tempLabel.setFont(new Font("Serif", Font.BOLD, 16));
        JLabel feelsLikeLabel = new JLabel();
        JLabel descLabel = new JLabel();
        JLabel humidityLabel = new JLabel();
        JLabel windSpeedLabel = new JLabel();
        JLabel iconLabel = new JLabel();

        weatherPanel.add(tempLabel);
        weatherPanel.add(feelsLikeLabel);
        weatherPanel.add(descLabel);
        weatherPanel.add(humidityLabel);
        weatherPanel.add(windSpeedLabel);
        weatherPanel.add(iconLabel);

        // 시간 선택을 위한 콤보 박스 생성
        JComboBox<String> timeComboBox = new JComboBox<>(timeList.toArray(new String[0]));
        timeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedTime = (String) timeComboBox.getSelectedItem();
                updateWeatherInfo(weatherMap.get(selectedTime), tempLabel, feelsLikeLabel, descLabel,
                        humidityLabel, windSpeedLabel, iconLabel);
            }
        });

        // 초기에는 첫 번째 시간대의 날씨 정보를 표시
        if (!timeList.isEmpty()) {
            updateWeatherInfo(weatherMap.get(timeList.get(0)), tempLabel, feelsLikeLabel, descLabel,
                    humidityLabel, windSpeedLabel, iconLabel);
        }

        // 상단에 시간 선택 콤보 박스를 배치할 패널 생성
        JPanel timePanel = new JPanel();
        timePanel.setLayout(new BorderLayout());
        timePanel.add(new JLabel("시간 선택: "), BorderLayout.WEST);
        timePanel.add(timeComboBox, BorderLayout.CENTER);

        // 게시판 패널 생성
        JPanel bulletinPanel = new JPanel();
        bulletinPanel.setLayout(new BorderLayout(10, 10));
        bulletinPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        bulletinPanel.setBackground(Color.LIGHT_GRAY);

        // 게시판 모델과 스크롤 패인 생성
        DefaultListModel<String> listModel = listModels.get(cityName);
        JList<String> messageList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(messageList);

        // 게시판 입력 필드와 버튼 생성
        JTextField messageField = new JTextField();
        JButton postButton = new JButton("게시");
        postButton.setBackground(new Color(30, 144, 255));
        postButton.setForeground(Color.WHITE);

        // 게시 버튼에 대한 이벤트 처리기 등록
        postButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = messageField.getText().trim();
                if (!message.isEmpty()) {
                    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    String messageWithTimestamp = message + " (" + timestamp + ")";
                    listModel.addElement(messageWithTimestamp);
                    bulletinBoards.get(cityName).add(messageWithTimestamp);
                    messageField.setText("");
                }
            }
        });

        // 입력 패널 생성
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout(10, 10));
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(postButton, BorderLayout.EAST);

        // 다이얼로그에 패널 추가
        bulletinPanel.add(scrollPane, BorderLayout.CENTER);
        bulletinPanel.add(inputPanel, BorderLayout.SOUTH);

        dialog.add(timePanel, BorderLayout.NORTH);
        dialog.add(weatherPanel, BorderLayout.CENTER);
        dialog.add(bulletinPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void updateWeatherInfo(JSONObject weatherData, JLabel tempLabel, JLabel feelsLikeLabel, JLabel descLabel, JLabel humidityLabel, JLabel windSpeedLabel, JLabel iconLabel) {
        double temperatureKelvin = weatherData.getJSONObject("main").getDouble("temp");
        String temperatureCelsius = kelvinToCelsius(temperatureKelvin);
        int weatherId = weatherData.getJSONArray("weather").getJSONObject(0).getInt("id");
        String description = wDescEngToKor(weatherId);
        double feelsLikeKelvin = weatherData.getJSONObject("main").getDouble("feels_like");
        String feelsLikeCelsius = kelvinToCelsius(feelsLikeKelvin);
        int humidity = weatherData.getJSONObject("main").getInt("humidity");
        double windSpeed = weatherData.getJSONObject("wind").getDouble("speed");
        String iconCode = weatherData.getJSONArray("weather").getJSONObject(0).getString("icon");
        String iconUrl = weatherIcon(iconCode);

        tempLabel.setText("Temperature: " + temperatureCelsius);
        feelsLikeLabel.setText("Feels Like: " + feelsLikeCelsius);
        descLabel.setText("Description: " + description);
        humidityLabel.setText("Humidity: " + humidity + "%");
        windSpeedLabel.setText("Wind Speed: " + windSpeed + " m/s");

        try {
            ImageIcon icon = new ImageIcon(new URL(iconUrl));
            Image img = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetBulletinBoards() {
        SwingUtilities.invokeLater(() -> {
            for (String city : CITIES) {
                bulletinBoards.get(city).clear();
                listModels.get(city).clear();
            }
        });
    }

    private static String getWeatherData(String city) throws Exception {
        String apiUrl = "https://api.openweathermap.org/data/2.5/forecast?q=" + city + "&appid=" + API_KEY;
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        } else {
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder jsonResponse = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                jsonResponse.append(inputLine);
            }
            in.close();
            return jsonResponse.toString();
        }
    }

    private static String kelvinToCelsius(double kelvin) {
        return String.format("%.1f°C", kelvin - 273.15);
    }

    private static String wDescEngToKor(int id) {
        switch(id) {
            case 800: return "맑음";
            case 801: return "구름 조금";
            case 802: return "구름 많음";
            case 803: return "흐림";
            case 804: return "매우 흐림";
            case 500: return "약한 비";
            case 501: return "비";
            case 502: return "강한 비";
            default: return "기타";
        }
    }

    private static String weatherIcon(String iconCode) {
        return "http://openweathermap.org/img/wn/" + iconCode + "@2x.png";
    }

    private static String convertUtcToLocalTime(String utcTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(utcTime, formatter);
        ZonedDateTime zonedUtcTime = localDateTime.atZone(ZoneId.of("UTC"));
        ZonedDateTime zonedLocalTime = zonedUtcTime.withZoneSameInstant(ZoneId.systemDefault());
        return zonedLocalTime.format(formatter);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WeatherApp app = new WeatherApp();
            app.setVisible(true);
        });
    }
}
