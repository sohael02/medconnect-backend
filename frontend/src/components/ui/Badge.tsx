import { cn } from '../../utils/helpers';

interface BadgeProps extends React.HTMLAttributes<HTMLSpanElement> {
  variant?: 'success' | 'warning' | 'error' | 'info' | 'gray';
  size?: 'sm' | 'md';
}

export function Badge({
  className,
  variant = 'gray',
  size = 'md',
  children,
  ...props
}: BadgeProps) {
  const variantClasses = {
    success: 'bg-green-100 text-green-800',
    warning: 'bg-yellow-100 text-yellow-800',
    error: 'bg-red-100 text-red-800',
    info: 'bg-blue-100 text-blue-800',
    gray: 'bg-secondary-100 text-secondary-800',
  };

  const sizeClasses = {
    sm: 'px-2 py-0.5 text-xs',
    md: 'px-2.5 py-0.5 text-xs',
  };

  return (
    <span
      className={cn(
        'inline-flex items-center rounded-full font-medium',
        variantClasses[variant],
        sizeClasses[size],
        className
      )}
      {...props}
    >
      {children}
    </span>
  );
}

interface StatusBadgeProps {
  status: string;
  showIcon?: boolean;
}

export function StatusBadge({ status, showIcon = false }: StatusBadgeProps) {
  const variants: Record<string, 'success' | 'warning' | 'error' | 'info' | 'gray'> = {
    IN_STOCK: 'success',
    OUT_OF_STOCK: 'error',
    LIMITED: 'warning',
    UNVERIFIED: 'gray',
    DISCONTINUED: 'gray',
  };

  const labels: Record<string, string> = {
    IN_STOCK: 'In Stock',
    OUT_OF_STOCK: 'Out of Stock',
    LIMITED: 'Limited Stock',
    UNVERIFIED: 'Availability Unverified',
    DISCONTINUED: 'Discontinued',
  };

  const icons: Record<string, string> = {
    IN_STOCK: 'check-circle',
    OUT_OF_STOCK: 'x-circle',
    LIMITED: 'alert-triangle',
    UNVERIFIED: 'help-circle',
    DISCONTINUED: 'archive',
  };

  const variant = variants[status] || 'gray';
  const label = labels[status] || status;

  return (
    <Badge variant={variant}>
      {showIcon && icons[status] && (
        <span className="mr-1" data-lucide={icons[status]} />
      )}
      {label}
    </Badge>
  );
}