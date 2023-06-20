package main

import (
	"log"
	"math/rand"
	"net/http"
	"strconv"
	"sync"
	"time"

	"github.com/gorilla/websocket"
)

var (
	clientData   sync.Map   // Mapa para almacenar los datos de cada cliente
	clientDataMu sync.Mutex // Mutex para el acceso concurrente al mapa
)

func main() {
	rand.Seed(time.Now().UnixNano())

	http.HandleFunc("/", handleWebSocket)
	err := http.ListenAndServe(":3003", nil)
	if err != nil {
		log.Fatal("Error al iniciar el servidor:", err)
	}
}

func handleWebSocket(w http.ResponseWriter, r *http.Request) {
	upgrader := websocket.Upgrader{}
	conn, err := upgrader.Upgrade(w, r, nil)
	if err != nil {
		log.Println("Error al actualizar a WebSocket:", err)
		return
	}
	defer conn.Close()

	log.Println("Socket creado")

	clientDataMu.Lock()
	clientData.Store(conn, 0) // Inicializar la variable del cliente en 0
	clientDataMu.Unlock()

	go sendRandomNumber(conn)
	receiveMessages(conn)

	clientDataMu.Lock()
	clientData.Delete(conn) // Eliminar la variable del cliente al cerrar la conexión
	clientDataMu.Unlock()

	log.Println("Conexión cerrada")
}

func sendRandomNumber(conn *websocket.Conn) {
	for {
		num := rand.Intn(10) + 95

		clientDataMu.Lock()
		value, ok := clientData.Load(conn)
		clientDataMu.Unlock()

		if ok {
			if numClient, ok := value.(int); ok {
				num += numClient
			}
		}

		err := conn.WriteJSON(num)
		if err != nil {
			log.Println("Error al enviar el mensaje:", err)
			break
		}

		log.Println("Dato enviado:", num)

		time.Sleep(time.Second)
	}
}

func receiveMessages(conn *websocket.Conn) {
	for {
		_, message, err := conn.ReadMessage()
		if err != nil {
			log.Println("Error al leer el mensaje del cliente:", err)
			break
		}

		log.Println("Mensaje recibido del cliente:", string(message))

		clientDataMu.Lock()
		_, ok := clientData.Load(conn)
		clientDataMu.Unlock()

		if ok {
			// Actualizar la variable del cliente sumando el mensaje recibido
			clientDataMu.Lock()
			clientData.Store(conn, parseMessageToInt(string(message)))
			clientDataMu.Unlock()
		}
	}
}

func parseMessageToInt(message string) int {
	num, err := strconv.Atoi(message)
	if err != nil {
		log.Println("Error al convertir el mensaje a entero:", err)
		return 0
	}
	return num
}
