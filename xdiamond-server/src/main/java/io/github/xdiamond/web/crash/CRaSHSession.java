/*
 * Copyright (C) 2012 eXo Platform SAS.
 * 
 * This is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation; either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this
 * software; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
 * Boston, MA 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package io.github.xdiamond.web.crash;

import org.crsh.shell.Shell;

import javax.websocket.Session;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

/** @author Julien Viet */
class CRaSHSession {

  /** . */
  final Session wsSession;

  /** . */
  final Shell shell;

  /** The current process being executed. */
  final AtomicReference<WSProcessContext> current;

  CRaSHSession(Session wsSession, Shell shell) {
    this.wsSession = wsSession;
    this.shell = shell;
    this.current = new AtomicReference<WSProcessContext>();
  }

  void send(String type) {
    send(type, null);
  }

  void send(String type, Object data) {
    send(new Event(type, data));
  }

  private void send(Event event) {
    try {
      wsSession.getBasicRemote().sendText(event.toJSON());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
