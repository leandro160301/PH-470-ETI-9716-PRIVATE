package com.jws.jwsapi.general.network

import com.jws.jwsapi.MainActivity
import com.jws.jwsapi.general.utils.Utils
import java.io.IOException
import java.net.DatagramPacket
import java.net.InetAddress
import java.net.MulticastSocket
import java.util.ArrayList

class SocketSearch(private val mainActivity: MainActivity) {
    private lateinit var actividadPrincipal: MainActivity
    public var listaDispositivos: MutableList<String> = ArrayList()

    init {
        actividadPrincipal = mainActivity
    }

    fun buscar(){
        val thread = Thread {
            try {
                val group = InetAddress.getByName("224.0.0.1") // Dirección de multidifusión
                val port = 8888
                val socket = MulticastSocket(port)
                socket.joinGroup(group)
                val message = "GET IP"
                val packet = DatagramPacket(message.toByteArray(), message.length, group, port)
                socket.send(packet)
                socket.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        thread.start()
    }

    fun init(){
        val thread = Thread {
            try {
                val group =
                    InetAddress.getByName("224.0.0.1")
                val port = 8888
                val socket = MulticastSocket(port)
                socket.joinGroup(group)
                val buffer = ByteArray(1024)
                val packet = DatagramPacket(buffer, buffer.size)

                while (true) {
                    socket.receive(packet)
                    val received = String(packet.data, 0, packet.length)
                    val senderIpAddress = packet.address.hostAddress
                    if (senderIpAddress != Utils.getIPAddress(true)) {
                        if(received=="GET IP"){
                            val responseMessage = Utils.getIPAddress(true)
                            val responsePacket = DatagramPacket(
                                responseMessage.toByteArray(),
                                responseMessage.length,
                                packet.address,
                                port
                            )
                            socket.send(responsePacket)
                        }else{
                            actividadPrincipal.runOnUiThread {
                                if(!listaDispositivos.contains(received)){
                                    listaDispositivos.add(received)
                                }
//                                actividadPrincipal.Mensaje(received, R.layout.item_customtoastok)

                            }
                        }
                    }

                    println("Mensaje recibido: $received")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        thread.start()
    }
}