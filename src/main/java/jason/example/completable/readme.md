CompletaleFuture is like JS Promise.

A promise  has two  possible states:
 
+ complete successfully 
+ complete abnormally


##Create Promise

```javascript

Promise p=new Promise(function(resolve, reject){
	console.log("resolve or reject");
})

```

```java
CompletableFuture<Integer> future1 = new CompletableFuture<>();

```


##Handle successful state
```javascript ```

We have three approaches to go to next stage.
+ convert the return value to another value. This is the thenApply function
+ just consume the value, and return nothing. This is the thenAccept 
+ Don't touch the value, and return nothing. This is the thenRun function.

Javascript doesn't differentiate these three type. If there is no value is returned, it is treated as "undefined"

##Handle Exception.
In javascript, we can attach exception handler using second parameter of then(ResultHandler, ErrorHandler), or catch(ErrorHandler).

CompletableFuture.handle() will execute an action no matter current stage complete successfully or abnormally. It is like a final error handler. Its can recover from error

CompletableFuture.whenComplete 



##What Thread to run?
This is a no-brainer for javascript since javascript just has one thread.

In java, we can have many options. First, there is a thread finishing current stage. so what thread do we use for next dependent thread?
+ thenApply: after this stage finishes, executes next stage immediately in DEFAULT thread. what is DEFAULT thread? MOSt LIKELY, it is CONTEXT thread: same thread finishing current thread.
+ thenApplyAsync. Schedule and execute it in DEFAULT async thread pool: ForkJoinPool.commonpool.
+ thenApplyAsync(action, executor): schedule and execute it with executor.






