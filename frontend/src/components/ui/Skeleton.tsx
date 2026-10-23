import { cn } from '../../utils/helpers';

interface SkeletonProps extends React.HTMLAttributes<HTMLDivElement> {
  variant?: 'text' | 'circular' | 'rectangular';
  width?: string | number;
  height?: string | number;
}

export function Skeleton({
  className,
  variant = 'text',
  width,
  height,
  ...props
}: SkeletonProps) {
  const variantClasses = {
    text: 'h-4 rounded',
    circular: 'rounded-full',
    rectangular: 'rounded-lg',
  };

  return (
    <div
      className={cn(
        'animate-pulse bg-secondary-200',
        variantClasses[variant],
        className
      )}
      style={{
        width: width ? (typeof width === 'number' ? `${width}px` : width) : undefined,
        height: height ? (typeof height === 'number' ? `${height}px` : height) : undefined,
      }}
      {...props}
    />
  );
}

export function SkeletonText({ lines = 3, className, ...props }: { lines?: number } & React.HTMLAttributes<HTMLDivElement>) {
  return (
    <div className={cn('space-y-2', className)} {...props}>
      {Array.from({ length: lines }).map((_, i) => (
        <Skeleton
          key={i}
          variant="text"
          width={i === lines - 1 ? '60%' : '100%'}
        />
      ))}
    </div>
  );
}

export function PharmacyCardSkeleton() {
  return (
    <div className="card p-4">
      <div className="flex gap-4">
        <Skeleton variant="circular" width={48} height={48} className="flex-shrink-0" />
        <div className="flex-1 min-w-0 space-y-3">
          <SkeletonText lines={2} />
          <Skeleton variant="rectangular" height={16} width="80%" />
          <Skeleton variant="rectangular" height={16} width="60%" />
        </div>
      </div>
    </div>
  );
}

export function SearchResultSkeleton({ count = 5 }) {
  return (
    <div className="space-y-4" role="status" aria-label="Loading search results">
      {Array.from({ length: count }).map((_, i) => (
        <PharmacyCardSkeleton key={i} />
      ))}
    </div>
  );
}