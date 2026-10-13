package com.dmm.sombra

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
Serve para **ligar o Hilt** na tua app.

O Hilt é a ferramenta que cria os objetos por ti (ViewModels, repositórios, base de dados) e os entrega a quem precisa. Para isso, tem de arrancar logo que a app abre, e esta classe é o sítio onde ele arranca.

- **`Application`**: é a primeira coisa que o Android cria ao abrir a app.
- **`@HiltAndroidApp`**: diz ao Hilt "começa aqui".

A classe fica vazia porque a anotação faz o trabalho todo. Sem ela, a app fecha ao abrir, porque tudo o que usa `@HiltViewModel` e `@Inject` deixa de funcionar.
 **/

@HiltAndroidApp
class SombraApplication : Application()
