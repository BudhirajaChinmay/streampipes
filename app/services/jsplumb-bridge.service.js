export class JsplumbBridge {

    constructor() {

    }

    setZoom(scale) {
        jsPlumb.setZoom(scale);
    }

    repaintEverything() {
        jsPlumb.repaintEverything(true);
    }

    deleteEveryEndpoint() {
        jsPlumb.deleteEveryEndpoint();
    }

    setContainer(container) {
        jsPlumb.setContainer(container);
    }

    unbind(element) {
        jsPlumb.unbind(element);
    }

    bind(event, fn) {
        return jsPlumb.bind(event, fn);
    }

    selectEndpoints() {
        return jsPlumb.selectEndpoints();
    }
    
    selectEndpoints(endpoint) {
        return jsPlumb.selectEndpoints(endpoint);
    }
    
    detach(connection) {
        jsPlumb.detach(connection);
    }

    getConnections(filter) {
        return jsPlumb.getConnections(filter);
    }
    
    addEndpoint(element, options) {
        return jsPlumb.addEndpoint(element, options);
    }
    
    connect(connection) {
        jsPlumb.connect(connection);
    }

    removeAllEndpoints(element) {
        jsPlumb.removeAllEndpoints(element);
    }

    registerEndpointTypes(typeInfo) {
        jsPlumb.registerEndpointTypes(typeInfo);
    }

    draggable(element, option) {
        jsPlumb.draggable(element, option);
    }
    
    setSuspendDrawing(bool1, bool2) {
        jsPlumb.setSuspendDrawing(bool1, bool2);
    }

    setSuspendDrawing(bool1) {
        jsPlumb.setSuspendDrawing(bool1);
    }
}