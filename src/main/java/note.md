#Concept

+ channel:a connection(socket, file) which is capable of read and write


##Channel
	
channel is a network connection. So it has local address, and remote address. It has states bound, connected/opened/closed. Channel has a channel pipeline which has logic to handle the inbound/outbound data. It is EventLoop which periodically checks the data associated with data and invoke ChannelPipeLine

Each operation in channel is returned with a future which is like a JS Promise
	
#Channel Handler Context

Channel data is handled by a channel pipline. How does a particular channel handler knows what is next handler in pipeline? This is coordinated by ChannelHandlerContext. It knows the pipeline composition and provides method so one handler can propagate to next handler.


	
	