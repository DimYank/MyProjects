class WsClients {
  constructor() {
    this.clientList = {};
    this.saveClient = this.saveClient.bind(this);
    this.deleteClient = this.deleteClient.bind(this);
  }

  saveClient(userId, socket) {
    this.clientList[userId] = socket;
  }

  deleteClient(userId) {
    delete this.clientList[userId];
  }

  get(userId) {
    return this.clientList[userId];
  }
}

export default WsClients;
