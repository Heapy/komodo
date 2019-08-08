package by.heap.undertow.http.client

/**
 * The basic way that it is used is:
 * - Connect to the remote host
 * - Create a ClientRequest object and
 *  populate it with headers etc
 * - Call ClientConnection.sendRequest with your request and a callback.
 *  When the connection is ready to send your request the callback will be invoked
 * - If you want a request body use ClientExchange.getRequestChannel() to get a channel to use for sending
 * - ClientExchange.setResponseListener() is used to register a
 *  listener to be notified of a response (you can also set handlers for HTTP 101 continue and server push events)
 * - Once the response listener has been invoked you can get the client response from the exchange
 */
