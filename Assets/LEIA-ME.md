# Sombra · recursos gráficos

## Pastas
- `svg/`       originais vetoriais (editáveis no Figma)
- `png/`       exportações @3x para README, Figma e Play Store (`logo_app_icon_512.png` = ícone da loja)
- `android/`   pronto a copiar para `app/src/main/res/`

## Android
1. Copia `android/drawable`, `android/mipmap-anydpi-v26` e `android/values/colors.xml` para `res/`.
2. O ícone adaptativo já inclui a versão monocromática (ícones temáticos do Android 13+).
3. Splash (Android 12+, API SplashScreen):
   `windowSplashScreenBackground = @color/sombra_ink`
   `windowSplashScreenAnimatedIcon = @drawable/splash_icon`
4. Notificações: `setSmallIcon(R.drawable.ic_notification)` (branco e plano, como o Android exige).
5. Em Compose: `Image(painterResource(R.drawable.weather_sol), contentDescription = "Céu limpo")`

## Mapa de ilustrações do tempo (código WMO da Open-Meteo)
| Ficheiro             | weather_code      |
|----------------------|-------------------|
| weather_sol          | 0, 1              |
| weather_sol_nublado  | 2                 |
| weather_nublado      | 3, 45, 48         |
| weather_chuva        | 51–67, 80–82, 95+ |
| weather_noite        | is_day = 0        |

## Logótipo com nome
Não há ficheiro do logótipo com o nome escrito, de propósito. Monta-o em Compose com
`logo_mark` + texto "Sombra" na fonte Bricolage Grotesque ExtraBold (Google Fonts),
para escalar bem e respeitar o tamanho de letra do sistema.
