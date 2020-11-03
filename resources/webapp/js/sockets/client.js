function _makeURL(host, uri, endpoint, params){
  const builder = [];
  const separator = '/';
  
  // HOST
  if( host && host.length > 0 ){
    if( host.charAt(host.length-1) === separator ){
      host = host.slice(0, host.length-1);
    }
    builder.push( host );
  }
  
  // URI
  if( uri && uri.length > 0 ){
    if( uri.charAt(0) === separator ){
      uri = uri.slice(1, uri.length);
    } 
    builder.push( separator );
    builder.push( uri );
  }
  
  // Endpoint
  if( endpoint && endpoint.length > 0 ){
    if( endpoint.charAt(0) === separator ){
      endpoint = endpoint.slice(1, endpoint.length);
    }
    builder.push( separator );
    builder.push( endpoint );
  }
  
  // Last Character
  if( builder.length === 1 ){
    const atLast = builder[builder.length-1];
    
    if( atLast && atLast.charAt(atLast.length-1) !== separator ){
      builder.push(separator);
    }
  }
  
  // Params
  if( params ){    
    // Query String
    builder.push('?');
    builder.push(
      Object.keys(params).map(function(key){
        return key+'='+encodeURIComponent(params[key]);
      }).join('&')
    );
  }
  
  // Made URL
  return builder.join('');
}

function ChatWS( _config ){
  
  let socket = null;
  
  // Configurations
  const config = Object.assign({}, {
    // For Connection
    url: "ws://localhost:3000/",
    endpoint: "chat",
    room: "lobby",
    protocol: null,
    params: {},
    
    // For Render
    form: document.body,
    
    // For Handlers
    onOpen: null,
    onMessage: null,
    onError: null,
  }, _config);
  
  const views = {}
  
  this.setSocket = function(v){ socket = v; }
  this.getSocket = function(){ return socket||null; }
  this.clearSocket = function(){ socket = null; }
  
  this.setConfig = function(k,v){ config[k] = v; }
  this.getConfig = function(k,d){ return config[k]||d; }

  this.setParams = function(v){ config.params = v||{}; }
  this.setParam = function(k,v){ config.params[k] = v; }
  this.getParam = function(k,d){ return config.params[k]||d; }

  this.setView = function(k,v){ views[k] = v; }
  this.getView = function(k,d){ return views[k]; }
}

ChatWS.prototype = (function(){
  // Connection
  function _connect(self, params){
    self.setParams(params);
    
    const URL = _makeURL(
      self.getConfig("url", null),
      self.getConfig("endpoint", null),
      self.getConfig("room", null),
      self.getConfig("params", null),
    );
    const protocol = self.getConfig("protocol", null);    
    
    // Create WebSocket
    const socket = new WebSocket(URL, protocol);
    
    // Set Configurations
    socket.binaryType = self.getConfig("binaryType", "arraybuffer");
    socket.bufferedAmount = self.getConfig("bufferedAmount", 0);

    // Receive Open
    const onOpen = self.getConfig("onOpen", null);
    if( onOpen && onOpen instanceof Function ){
      socket.onopen = function(event){
        onOpen(self, event);
      };
    }
  
    // Receive Message
    const onMessage = self.getConfig("onMessage", null);
    if( onMessage && onMessage instanceof Function ){
      socket.onmessage = function(event){
        onMessage(self, event);
      };
    }

    // Receive Close
    const onClose = self.getConfig("onClose", null);
    if( onClose && onClose instanceof Function ){
      socket.onClose = function(event){
        onClose(self, event);
      }
    }

    // Receive Error
    const onError = self.getConfig("onError", null);
    if( onError && onError instanceof Function ){
      socket.onerror = function(event){
        onError(self, event);
      }
    }
    
    // Store Socket
    self.setSocket(socket);
  }
  
  // Disconnection
  function _disconnect(self){
    const socket = self.getSocket();
    
    if( socket && socket.readyState == socket.OPEN ){
      socket.close();
    }
    
    self.setConfig("socket", null);
  }
  
  // Get Socket State
  function _getState(self){
    const codeMap = [
      {
        code: 0,
        name: "CONNECTING",
        desc: "연결이 수립되지 않은 상태입니다."
      },
      {
        code: 1,
        name: "OPEN",
        desc: "연결이 수립되어 데이터가 오고갈 수 있는 상태입니다.",
      },
      {
        code: 2,
        name: "CLOSING",
        desc: "연결이 닫히는 중 입니다.",
      },
      {
        code: 3,
        name: "CLOSED",
        desc: "연결이 종료되었거나, 연결에 실패한 경우입니다.",
      }
    ];
    
    const socket = self.getSocket();
    if( !socket ){
      return null;
    }
    return codeMap.filter( data => data.code === socket.readyState )[0]||null;
  }
  
  // Message Handlers
  function _send(self, message){
    const socket = self.getSocket();
    
    socket.send(message);
  }
  function _sendJson(self, message){
    _send(self, JSON.stringify(message));
  }
  function _sendMessage(self, message){
    _send(self, JSON.stringify({
      content: message
    }));
  }
  
  // Mathods
  return {
    connect: function(params){
      const socket = this.getSocket();
      if( socket && socket.readyState === socket.OPEN ){
        return socket;
      }
      
      this.setSocket(null);
      
      return _connect(this, params);
    },
    disconnect: function(){
      _disconnect(this);
    },
    send: function(message){
      //_send(this, message);
      _sendMessage(this, message);
    },
    sendMessage: function(message){
      _sendMessage(this, message);
    },
    sendJson: function(message){
      _sendJson(this, message);
    },
    getState: function(){
      return _getState(this);
    },
  };
})();