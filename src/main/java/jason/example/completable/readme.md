Do you know javascript Promise? CompletableFuture is then very easy for you to understand.

A promise  has two  possible states:
 
+ complete successfully 
+ complete abnormally


##Create and Complete Promise

```javascript

	Promise p=new Promise(function(resolve, reject){
		resolve(5);
		//or 
		reject("error");
	})
```

```java

	CompletableFuture<Integer> future1 = new CompletableFuture<>();
	Executor executor = Executors.newFixedThreadPool(5);
	executor.execute(()-> { 
		future1.complete(5);
		//or 
		future1.completeExceptionally(new RuntimeException("error));
	});
```


##Handle successful state

```javascript 
	
	p.then(intValue=>intValue*2)
```

We have three approaches to go to next stage in java.
+ convert the return value to another value. This is the thenApply function
```java

	future1.thenApply( (intValue)->intValue*2);
```
+ just consume the value, and return nothing. This is the thenAccept
```java

	future1.thenAccept((intValue)->System.out.println(intValue));
``` 
+ Don't touch the value, and return nothing. This is the thenRun function.
```java

	future1.thenRun(()->System.out.println(intValue));
```

Javascript doesn't differentiate these three types. If there is no value is returned, it is treated as "undefined"

##Handle Exception.
```javascript

	p.then(intValue=>intValue*2, error=>console.log("error"));
	//or 
	
	p.catch(error=>console.log("error"));
```
```java

	future1.handle((intValue, exception)->{
		if (exception!=null) {
			System.out.println(exception.getMessage());
			return 6; //error recover
		} else {
			return intValue*2;
		}
	});
	
	
	CompletableFuture<Integer> future2 = future1.whenComplete( (intValue, exception)-> {
	
		//return nothing.
		if (exception!=null) {
			System.out.println(exception.getMessage());
		}
	});
	
	//no future is changed for whenComplete
	assert future2 == future1
```

##What Thread to run?
This is a no-brainer for javascript since javascript just has one thread.

In java, we can have many options. First, there is a thread finishing current stage. so what thread do we use for next dependent stage?
+ thenApply: after this stage finishes, executes next stage immediately in **DEFAULT** thread. what is DEFAULT thread? it is CONTEXT thread: same thread finishing current task. **immediately** here doesn't gunruantte synchronously execution.

```java

	future1.thenApply(intValue->inteValue*2);
```

+ thenApplyAsync. Schedule and execute it in DEFAULT async thread pool: ForkJoinPool.commonpool.

```java

	future1.thenApplyAsync(intValue->inteValue*2);
```

+ thenApplyAsync(action, executor): schedule and execute it with executor.

```java

	future1.thenApplyAsync(intValue->inteValue*2, executor);
``

##Chain
```javascript

	p.then(intValue=>intValue*2).then(intValue=>intValue/2);
```

```java

	future1.thenApply(intValue->intValue*2).thenApply(intValue=>intValue/2);
```


##Nested Chain
```javascript

	p.then( intValue=> {
	
		//the value is computed in future.
		return new Promise(function(resolve, reject){
			resolve(intValue*2);
		});
	});
```

```java

	future1.thenCompose(intValue=>{
	
		CompletableFuture<Integer> ret = new CompletableFuture<>();
		executor.execute(()->{
			ret.complete(inttValue*2);
		});
		return ret;
	});
```

##Compose and synchronization.
```javascript

	Promise.all(p1, p2).then(values=>console.log(values));
	
	Promise.race(p1, p2).then(intValue=>intValue*2);
```

```java

	CompletableFuture<Integer>[] futures= new CompletableFuture[]{future1, future2, future3};
	CompletableFuture.allOf(futures).thenApply(v->{
		//v is void here. 
		
		/* 
		we get the value from context: futuress.
		future.get() will not block since this piece code is called in thenApply
		*/
		
		return Arrays.stream(futures).map(future->future.get()).collect(Collectors.toList());
	}); 
	
	
	CompletableFuture.anyOf(futures).thenApply(intValue->intValue*2);
```
if there is only two Promise, java CompletableFuture also has instance shortCut methods to perform combine.

##Powerful synchronization.
CompletableFuture is another mechanism to execute concurrent tasks.

+ attaches two thenApply to a CompletableFuture is like a fork mechanism,
+ allOf, anyOf is like a latch. 

We can have an ad hoc execution task graph.




