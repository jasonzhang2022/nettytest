package jason.example.completable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WhichThreadToRun {
	
	public static void main(String[] args){
		ExecutorService executor = Executors.newFixedThreadPool(1);

		CompletableFuture<Integer> future1 = new CompletableFuture();
		executor.execute(() -> {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.printf("complete task 1 by executor thread %s\n", Thread.currentThread().getName());
			future1.complete(1);
		});

		
		
		
		CompletableFuture<String> future11 = future1.thenApplyAsync(IntValue -> {

			System.out.printf("complete task 11 asynchronusly by Exector thread %s.\n", Thread.currentThread().getName());
			return IntValue + " fork11";
		}, executor);
		
		/*
		 * task 12 will be completed in the same thread as task 1 completion thread, that is executor thread.
		 */
		CompletableFuture<String> future12 = future1.thenApply(IntValue -> {
			System.out.printf("complete task 12 synchronusly by thread %s.\n", Thread.currentThread().getName());
			return IntValue + " fork12";
		});
		
		
		
		
		/*
		 * future12 us executed by default async thread pool which is ForkJoinPool.commonPool.
		 */
		CompletableFuture<String> future13 = future1.thenApplyAsync(IntValue -> {
			System.out.printf("finish task 13 at thread %s. Scheduled without executor\n", Thread.currentThread().getName());
			return IntValue + " fork13";
		});
		
		CompletableFuture.allOf(future11, future12, future13).thenRun(()->{
			executor.shutdown();
		});
		System.out.println("main thread finished");
	}

}
