/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rpp;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.text.StyledDocument;

/**
 *
 * @author shorifuzzaman
 */
public class message implements Serializable {
    String type;
    String username;
    String message;
    StyledDocument doc;
    Color color;
    ArrayList<message> clients;
    message(String type, StyledDocument doc ){
        this.type=type;
        this.doc=doc;
    }
    message(String type, String username, String message){
        this.type=type;
        this.username=username;
        this.message=message;
    }
    message(String message){
        this.message=message;
    }
    message(String type, String username, Color color){
        this.type=type;
        this.username=username;
        this.color=color;
    }
    message(String type,ArrayList<message> clients){
        this.type=type;
        this.clients=clients;
    }
    
}

